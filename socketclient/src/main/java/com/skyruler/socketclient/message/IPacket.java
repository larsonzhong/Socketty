package com.skyruler.socketclient.message;

public interface IPacket {
    short getLength() ;

    byte[] getData() ;

    byte[] getBytes() ;


}
