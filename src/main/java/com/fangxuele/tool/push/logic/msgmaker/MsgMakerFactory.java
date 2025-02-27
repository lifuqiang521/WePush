package com.fangxuele.tool.push.logic.msgmaker;

import com.fangxuele.tool.push.App;
import com.fangxuele.tool.push.logic.MessageTypeEnum;

/**
 * <pre>
 * 消息加工器工厂类
 * </pre>
 *
 * @author <a href="https://github.com/rememberber">RememBerBer</a>
 * @since 2019/6/15.
 */
public class MsgMakerFactory {

    /**
     * 获取消息加工器
     *
     * @return IMsgMaker
     */
    public static IMsgMaker getMsgMaker() {
        IMsgMaker iMsgMaker = null;
        switch (App.config.getMsgType()) {
            case MessageTypeEnum.MP_TEMPLATE_CODE:
                iMsgMaker = new WxMpTemplateMsgMaker();
                break;
            case MessageTypeEnum.MA_TEMPLATE_CODE:
                iMsgMaker = new WxMaTemplateMsgMaker();
                break;
            case MessageTypeEnum.KEFU_CODE:
                iMsgMaker = new WxKefuMsgMaker();
                break;
            case MessageTypeEnum.ALI_YUN_CODE:
                iMsgMaker = new AliyunMsgMaker();
                break;
            case MessageTypeEnum.ALI_TEMPLATE_CODE:
                iMsgMaker = new AliTemplateMsgMaker();
                break;
            case MessageTypeEnum.TX_YUN_CODE:
                iMsgMaker = new TxYunMsgMaker();
                break;
            case MessageTypeEnum.YUN_PIAN_CODE:
                iMsgMaker = new YunPianMsgMaker();
                break;
            case MessageTypeEnum.EMAIL_CODE:
                iMsgMaker = new MailMsgMaker();
                break;
            case MessageTypeEnum.WX_CP_CODE:
                iMsgMaker = new WxCpMsgMaker();
                break;
            case MessageTypeEnum.HTTP_CODE:
                iMsgMaker = new HttpMsgMaker();
                break;
            default:
        }
        return iMsgMaker;
    }
}
