package com.seer.operation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("seer_block_sync")
public class BlockSyncPo implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private Integer blockSync;
    private String memo;
    private Integer status;//0 正常；1 暂停
    private Integer saveBlock;//是否保存区块信息，0 保存；1 不保存
    private Integer saveTx; // 是否保存交易信息，0 保存；1 不保存
}
