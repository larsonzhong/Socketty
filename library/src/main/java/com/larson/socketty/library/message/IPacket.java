package com.larson.socketty.library.message;

public interface IPacket {
    short getLength() ;

    byte[] getData() ;

    byte[] getBytes() ;


}
