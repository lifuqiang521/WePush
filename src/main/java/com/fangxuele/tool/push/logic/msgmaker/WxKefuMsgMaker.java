package com.fangxuele.tool.push.logic.msgmaker;

import com.fangxuele.tool.push.logic.msgsender.WxMpTemplateMsgSender;
import com.fangxuele.tool.push.ui.form.msg.KefuMsgForm;
import com.fangxuele.tool.push.util.TemplateUtil;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import org.apache.velocity.VelocityContext;

/**
 * <pre>
 * 微信客服消息加工器
 * </pre>
 *
 * @author <a href="https://github.com/rememberber">Zhou Bo</a>
 * @since 2019/6/14.
 */
public class WxKefuMsgMaker extends BaseMsgMaker implements IMsgMaker {

    private static String msgKefuMsgType;

    private static String msgKefuMsgTitle;

    private static String msgKefuPicUrl;

    private static String msgKefuDesc;

    private static String msgKefuUrl;

    /**
     * 准备(界面字段等)
     */
    @Override
    public void prepare() {
        msgKefuMsgType = KefuMsgForm.getInstance().getMsgKefuMsgTypeComboBox().getSelectedItem().toString();
        msgKefuMsgTitle = KefuMsgForm.getInstance().getMsgKefuMsgTitleTextField().getText();
        msgKefuPicUrl = KefuMsgForm.getInstance().getMsgKefuPicUrlTextField().getText();
        msgKefuDesc = KefuMsgForm.getInstance().getMsgKefuDescTextField().getText();
        msgKefuUrl = KefuMsgForm.getInstance().getMsgKefuUrlTextField().getText();
        WxMpTemplateMsgSender.wxMpConfigStorage = null;
        WxMpTemplateMsgSender.wxMpService = null;
    }

    /**
     * 组织客服消息
     *
     * @param msgData 消息信息
     * @return WxMpKefuMessage
     */
    @Override
    public WxMpKefuMessage makeMsg(String[] msgData) {

        WxMpKefuMessage kefuMessage = null;
        VelocityContext velocityContext = getVelocityContext(msgData);
        if ("图文消息".equals(msgKefuMsgType)) {
            WxMpKefuMessage.WxArticle article = new WxMpKefuMessage.WxArticle();

            // 标题
            String title = TemplateUtil.evaluate(msgKefuMsgTitle, velocityContext);
            article.setTitle(title);

            // 图片url
            article.setPicUrl(msgKefuPicUrl);

            // 描述
            String description = TemplateUtil.evaluate(msgKefuDesc, velocityContext);
            article.setDescription(description);

            // 跳转url
            String url = TemplateUtil.evaluate(msgKefuUrl, velocityContext);
            article.setUrl(url);

            kefuMessage = WxMpKefuMessage.NEWS().addArticle(article).build();
        } else if ("文本消息".equals(msgKefuMsgType)) {
            String content = TemplateUtil.evaluate(msgKefuMsgTitle, velocityContext);
            kefuMessage = WxMpKefuMessage.TEXT().content(content).build();
        }

        return kefuMessage;
    }
}
