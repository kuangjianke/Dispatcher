package com.renren.test.basic;

import java.io.UnsupportedEncodingException;

import com.mop.dbstorm.zookeeper.DataDeserializer;
import com.mop.dbstorm.zookeeper.DataSerializer;
import com.mop.dbstorm.zookeeper.exception.ZKDataDeserializeException;
import com.mop.dbstorm.zookeeper.exception.ZKDataSerializeException;

public class TestZKHelper {

    public static DataDeserializer<String> zkDeSer = new DataDeserializer<String>() {

                                                       @Override
                                                       public String deserialize(byte[] data)
                                                           throws ZKDataDeserializeException {
                                                           try {
                                                               return new String(data, "utf-8");
                                                           } catch (UnsupportedEncodingException e) {
                                                               new ZKDataDeserializeException(e);
                                                           }
                                                           return null;
                                                       }
                                                   };

    public static DataSerializer<String>   zkSer   = new DataSerializer<String>() {

                                                       @Override
                                                       public byte[] serialize(String obj)
                                                           throws ZKDataSerializeException {
                                                           try {
                                                               return obj.getBytes("utf-8");
                                                           } catch (UnsupportedEncodingException e) {
                                                               new ZKDataSerializeException(e);
                                                           }
                                                           return null;
                                                       }
                                                   };

}
