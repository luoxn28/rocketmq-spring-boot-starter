package com.luo.boot.rocketmq.test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author xiangnan
 * date 2018/8/6 15:25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message implements Serializable {
    private int id;
    private String name;
}
