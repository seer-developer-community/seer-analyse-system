package com.seer.operation.rpcClient;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seer.operation.rpcClient.json.Base64Coder;
import com.seer.operation.rpcClient.json.Json;
import com.seer.operation.rpcClient.response.*;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SeerJsonRpcClient implements SeerRpcClient {
    private static final Logger logger = Logger.getLogger(SeerRpcClient.class.getCanonicalName());
    public URL rpcURL;
    private URL noAuthURL;
    private HostnameVerifier hostnameVerifier;
    private SSLSocketFactory sslSocketFactory;
    private String authStr;
    public static final Charset QUERY_CHARSET = Charset.forName("UTF-8");//ISO8859-1

    public SeerJsonRpcClient(String ip, String port) {
        try {
            URL url = new URL("http://admin:admin@" + ip + ":" + port + "/");
            init(url);
        } catch (MalformedURLException e) {
            logger.log(Level.FINE, "create URL error！");
        }
    }

    public SeerJsonRpcClient(URL rpc) {
        init(rpc);
    }

    public void init(URL rpc) {
        this.hostnameVerifier = null;
        this.sslSocketFactory = null;
        this.rpcURL = rpc;

        try {
            this.noAuthURL = (new URI(rpc.getProtocol(), (String) null, rpc.getHost(), rpc.getPort(), rpc.getPath(), rpc.getQuery(), (String) null)).toURL();
        } catch (URISyntaxException | MalformedURLException var3) {
            throw new IllegalArgumentException(rpc.toString(), var3);
        }

        this.authStr = rpc.getUserInfo() == null ? null : String.valueOf(Base64Coder.encode(rpc.getUserInfo().getBytes(Charset.forName("ISO8859-1"))));
    }

    public byte[] prepareRequest(final String method, final Object... params) {
        return Json.stringify(new LinkedHashMap() {
            {
                this.put("method", method);
                this.put("params", params);
                this.put("id", "1");
            }
        }).getBytes(QUERY_CHARSET);
    }

    public Object query(String method, Object... o) {
        HttpURLConnection conn;
        try {
            conn = (HttpURLConnection) noAuthURL.openConnection();

            conn.setDoOutput(true);
            conn.setDoInput(true);

            if (conn instanceof HttpsURLConnection) {
                if (hostnameVerifier != null)
                    ((HttpsURLConnection) conn).setHostnameVerifier(hostnameVerifier);
                if (sslSocketFactory != null)
                    ((HttpsURLConnection) conn).setSSLSocketFactory(sslSocketFactory);
            }
            ((HttpURLConnection) conn).setRequestProperty("Authorization", "Basic " + authStr);
            byte[] r = prepareRequest(method, o);
            logger.log(Level.FINE, "JSON-RPC request:\n{0}", new String(r, QUERY_CHARSET));
            conn.getOutputStream().write(r);
            conn.getOutputStream().close();
            int responseCode = conn.getResponseCode();
            if (responseCode != 200)
                throw new RpcException(method, Arrays.deepToString(o), responseCode, conn.getResponseMessage(), new String(loadStream(conn.getErrorStream(), true)));
            return loadResponse(conn.getInputStream(), "1", true);
        } catch (IOException ex) {
            logger.log(Level.FINE, "RPC Query Failed.请检查RPC是否已开启");
            return null;
//            throw new RpcException(method, Arrays.deepToString(o), ex);
        }
    }

    private static byte[] loadStream(InputStream in, boolean close) throws IOException {
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        for (; ; ) {
            int nr = in.read(buffer);

            if (nr == -1)
                break;
            if (nr == 0)
                throw new IOException("Read timed out");

            o.write(buffer, 0, nr);
        }
        return o.toByteArray();
    }

    public Object loadResponse(InputStream in, Object expectedID, boolean close) throws IOException, RpcException {
        try {
            String r = new String(loadStream(in, close), QUERY_CHARSET);
            logger.log(Level.FINE, "JSON-RPC response:\n{0}", r);
            try {
                Map response = (Map) Json.parse(r);

                if (!expectedID.equals(response.get("id").toString()))
                    throw new RpcException("Wrong response ID (expected: " + String.valueOf(expectedID) + ", response: " + response.get("id") + ")");

                if (response.get("error") != null)
                    throw new RpcException(Json.stringify(response.get("error")));

                return response.get("result");
            } catch (ClassCastException ex) {
                throw new RpcException("Invalid server response format (data: \"" + r + "\")");
            }
        } finally {
            if (close)
                in.close();
        }
    }

    @Override
    public BlockInfo blockInfo() {
        Object object = query("info");
        String jsonString = JSONObject.toJSONString(object);
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        return JSONObject.toJavaObject(jsonObject, BlockInfo.class);
    }

    @Override
    public Block getBlock(String var1) {
        Object object = query("get_block", var1);
        String jsonString = JSONObject.toJSONString(object);
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        return JSONObject.toJavaObject(jsonObject, Block.class);
    }

    @Override
    public GetAsset getAsset(String var1) {
        Object object = query("get_asset", var1);
        String jsonString = JSONObject.toJSONString(object);
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        GetAsset asset = new GetAsset(jsonObject);
        return asset;
    }

    @Override
    public GetAccount getAccount(String var1) {
        Object object = query("get_account", var1);
        String jsonString = JSONObject.toJSONString(object);
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        return JSONObject.toJavaObject(jsonObject, GetAccount.class);
    }

    @Override
    public GetGlobal getGlobal() {
        Object object = query("get_global_properties", "");
        String jsonString = JSONObject.toJSONString(object);
        logger.info("global:" + jsonString);
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        try {
            return JSONObject.toJavaObject(jsonObject, GetGlobal.class);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public SeerRoom getSeerRoom(String roomId, int start, int limit) {
        Object object = query("get_seer_room", roomId, start, limit);
        String jsonString = JSONObject.toJSONString(object);
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        return JSONObject.toJavaObject(jsonObject, SeerRoom.class);
    }

    @Override
    public JSONArray getVestingBalances(String id) {
        Object object = query("get_vesting_balances", id);
        String jsonString = JSONObject.toJSONString(object);
        JSONArray array = JSON.parseArray(jsonString);
        return array;
    }

    @Override
    public HouseByAccount getHouseByAccount(String account) {
        Object object = query("get_house_by_account", account);
        String jsonString = JSONObject.toJSONString(object);
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        return JSONObject.toJavaObject(jsonObject, HouseByAccount.class);
    }
}
