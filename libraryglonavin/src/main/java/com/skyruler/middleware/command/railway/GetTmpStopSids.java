package com.skyruler.middleware.command.railway;

import com.skyruler.middleware.command.AbsCommand;
import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.message.AckMode;
import com.skyruler.socketclient.message.IMessage;

public class GetTmpStopSids extends AbsCommand {
    private static final byte ID = (byte) 0x82;
    private static final byte RESP_ID = (byte) 0x83;
    private final TmpStopSidCallBack callBack;

    public interface TmpStopSidCallBack {
        /**
         * @param sids 按小到大顺序添加停车的站点 ID
         */
        void handleSiteIds(byte[] sids);
    }

    public GetTmpStopSids(TmpStopSidCallBack callBack) {
        super(ID, RESP_ID, AckMode.MESSAGE);
        this.callBack = callBack;
        super.body = new byte[]{};
    }

    @Override
    public MessageFilter getResultHandler() {
        return new MessageFilter() {
            @Override
            public boolean accept(IMessage msg) {
                if (msg.getBody() == null || msg.getBody().length <= 0) {
                    return false;
                }
                byte[] siteIds = msg.getBody();
                callBack.handleSiteIds(siteIds);
                return true;
            }
        };
    }
}
