package com.skyruler.middleware.command.railway;

import com.skyruler.middleware.command.AbsCommand;
import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.message.AckMode;
import com.skyruler.socketclient.message.IMessage;

import java.util.Arrays;

public class DeviceMode extends AbsCommand {
    private static final byte ID = 0x30;
    private static final byte RESP_ID = 0x31;
    private final String name;

    public enum Mode {
        SelfCheck("自检模式", (byte) 0x03),
        Railway("高铁模式", (byte) 0x04),
        Road("公路模式", (byte) 0x05);

        private final String name;
        private final byte mode;

        Mode(String name, byte mode) {
            this.name = name;
            this.mode = mode;
        }
    }

    public DeviceMode(Mode locMode) {
        super(ID, RESP_ID, AckMode.MESSAGE);
        super.body = new byte[]{locMode.mode};
        this.name = locMode.name;
    }

    @Override
    public String toString() {
        return "DeviceMode{" +
                "name=" + name +
                "mode=" + Arrays.toString(body) +
                '}';
    }

    @Override
    public MessageFilter getResultHandler() {
        // 高铁的模式选择成功返回和地铁人员有区别，高铁是返回对应的modeCode即为成功
        return new MessageFilter() {
            @Override
            public boolean accept(IMessage msg) {
                return msg != null && body[0] == msg.getBody()[0];
            }
        };
    }
}
