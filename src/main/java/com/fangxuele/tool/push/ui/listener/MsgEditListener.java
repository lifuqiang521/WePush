package com.fangxuele.tool.push.ui.listener;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.fangxuele.tool.push.App;
import com.fangxuele.tool.push.logic.MessageTypeEnum;
import com.fangxuele.tool.push.logic.PushControl;
import com.fangxuele.tool.push.logic.msgsender.HttpSendResult;
import com.fangxuele.tool.push.logic.msgsender.SendResult;
import com.fangxuele.tool.push.ui.UiConsts;
import com.fangxuele.tool.push.ui.dialog.CommonTipsDialog;
import com.fangxuele.tool.push.ui.form.HttpResultForm;
import com.fangxuele.tool.push.ui.form.MainWindow;
import com.fangxuele.tool.push.ui.form.MessageEditForm;
import com.fangxuele.tool.push.ui.form.MessageManageForm;
import com.fangxuele.tool.push.ui.form.msg.MsgFormFactory;
import com.fangxuele.tool.push.ui.form.msg.WxCpMsgForm;
import com.fangxuele.tool.push.ui.frame.HttpResultFrame;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * <pre>
 * 编辑消息tab相关事件监听
 * </pre>
 *
 * @author <a href="https://github.com/rememberber">RememBerBer</a>
 * @since 2017/6/18.
 */
public class MsgEditListener {
    private static final Log logger = LogFactory.get();

    public static void addListeners() {
        JSplitPane messagePanel = MainWindow.getInstance().getMessagePanel();
        MessageEditForm messageEditForm = MessageEditForm.getInstance();

        // 保存按钮事件
        messageEditForm.getMsgSaveButton().addActionListener(e -> {
            String msgName = messageEditForm.getMsgNameField().getText();
            if (StringUtils.isBlank(msgName)) {
                JOptionPane.showMessageDialog(messagePanel, "请填写消息名称！\n\n", "失败",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                MsgFormFactory.getMsgForm().save(msgName);

                App.config.setPreviewUser(messageEditForm.getPreviewUserField().getText());
                App.config.save();
                MessageManageForm.init();
            } catch (Exception e1) {
                JOptionPane.showMessageDialog(messagePanel, "保存失败！\n\n" + e1.getMessage(), "失败",
                        JOptionPane.ERROR_MESSAGE);
                logger.error(e1);
            }

        });

        // 预览按钮事件
        messageEditForm.getPreviewMsgButton().addActionListener(e -> {
            try {
                if (App.config.getMsgType() != MessageTypeEnum.HTTP_CODE && "".equals(messageEditForm.getPreviewUserField().getText().trim())) {
                    JOptionPane.showMessageDialog(messagePanel, "预览用户不能为空！", "提示",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                if (App.config.getMsgType() == MessageTypeEnum.MA_TEMPLATE_CODE
                        && messageEditForm.getPreviewUserField().getText().split(";")[0].length() < 2) {
                    JOptionPane.showMessageDialog(messagePanel, "小程序模板消息预览时，“预览用户openid”输入框里填写openid|formId，\n" +
                                    "示例格式：\n" +
                                    "opd-aswadfasdfasdfasdf|fi291834543", "提示",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                if (App.config.getMsgType() == MessageTypeEnum.WX_CP_CODE
                        && WxCpMsgForm.getInstance().getAppNameComboBox().getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(MainWindow.getInstance().getMessagePanel(), "请选择应用！", "成功",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                List<SendResult> sendResultList = PushControl.preview();
                if (sendResultList != null) {

                    StringBuilder tipsBuilder = new StringBuilder();
                    int totalCount = messageEditForm.getPreviewUserField().getText().split(";").length;
                    long successCount = sendResultList.stream().filter(SendResult::isSuccess).count();
                    if (totalCount == successCount) {
                        tipsBuilder.append("<h1>发送预览消息成功！</h1>");
                    } else if (successCount == 0) {
                        tipsBuilder.append("<h2>发送预览消息失败！</h2>");
                    } else {
                        tipsBuilder.append("<h2>有部分预览消息发送失败！</h2>");
                    }
                    sendResultList.stream().filter(sendResult -> !sendResult.isSuccess())
                            .forEach(sendResult -> tipsBuilder.append("<p>").append(sendResult.getInfo()).append("</p>"));

                    if (App.config.getMsgType() == MessageTypeEnum.HTTP_CODE && totalCount == successCount) {
                        HttpSendResult httpSendResult = (HttpSendResult) sendResultList.get(0);
                        HttpResultForm.getInstance().getBodyTextArea().setText(httpSendResult.getBody());
                        HttpResultForm.getInstance().getHeadersTextArea().setText(httpSendResult.getHeaders());
                        HttpResultForm.getInstance().getCookiesTextArea().setText(httpSendResult.getCookies());
                        HttpResultFrame.showResultWindow();
                    } else {
                        CommonTipsDialog dialog = new CommonTipsDialog();
                        dialog.setHtmlText(tipsBuilder.toString());
                        dialog.pack();
                        dialog.setVisible(true);
                    }

                    // 保存累计推送总数
                    App.config.setPushTotal(App.config.getPushTotal() + successCount);
                    App.config.save();
                }
            } catch (Exception e1) {
                JOptionPane.showMessageDialog(messagePanel, "发送预览消息失败！\n\n" + e1.getMessage(), "失败",
                        JOptionPane.ERROR_MESSAGE);
                logger.error(e1);
            }
        });

        messageEditForm.getPreviewUserHelpLabel().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                CommonTipsDialog dialog = new CommonTipsDialog();

                int msgType = App.config.getMsgType();
                String fillParaName = "";
                String paraDemo = "";
                if (msgType == MessageTypeEnum.MP_TEMPLATE_CODE || msgType == MessageTypeEnum.KEFU_PRIORITY_CODE
                        || msgType == MessageTypeEnum.KEFU_CODE) {
                    fillParaName = "预览消息用户的openId";
                    paraDemo = "ox_kxwS_gGt63adS-zemlETtuvw1;ox_kxwS_gGt63adS-zemlETtuvw2";
                } else if (msgType == MessageTypeEnum.MA_TEMPLATE_CODE) {
                    fillParaName = "预览消息用户的openId|formId";
                    paraDemo = "opd-aswadfasdfasdfasdf|fi291834543;opd-aswadfasdfasdfasdf2|fi2918345432";
                } else if (msgType == MessageTypeEnum.EMAIL_CODE) {
                    fillParaName = "预览消息用户的邮箱地址";
                    paraDemo = "abc@163.com;def@163.com";
                } else if (msgType == MessageTypeEnum.WX_CP_CODE) {
                    fillParaName = "预览消息用户的UserId";
                    paraDemo = "zhoubo;rememberber";
                } else if (msgType == MessageTypeEnum.HTTP_CODE) {
                    fillParaName = "消息变量(如果是变量消息)";
                    paraDemo = "变量0|变量1|变量2";
                } else if (msgType == MessageTypeEnum.ALI_YUN_CODE || msgType == MessageTypeEnum.ALI_TEMPLATE_CODE
                        || msgType == MessageTypeEnum.TX_YUN_CODE || msgType == MessageTypeEnum.YUN_PIAN_CODE) {
                    fillParaName = "预览消息用户的手机号";
                    paraDemo = "13910733521;13910733522";
                }
                StringBuilder tipsBuilder = new StringBuilder();
                tipsBuilder.append("<h1>如何填写？</h1>");
                tipsBuilder.append("<h2>此处填写").append(fillParaName).append("</h2>");
                if (msgType != MessageTypeEnum.HTTP_CODE) {
                    tipsBuilder.append("<p>如有多个，请以半角分号分隔</p>");
                }
                tipsBuilder.append("<p>示例：</p>");
                tipsBuilder.append("<p>").append(paraDemo).append("</p>");

                dialog.setHtmlText(tipsBuilder.toString());
                dialog.pack();
                dialog.setVisible(true);

                super.mousePressed(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                JLabel label = (JLabel) e.getComponent();
                label.setCursor(new Cursor(Cursor.HAND_CURSOR));
                label.setIcon(new ImageIcon(UiConsts.HELP_FOCUSED_ICON));
                super.mouseEntered(e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                JLabel label = (JLabel) e.getComponent();
                label.setIcon(new ImageIcon(UiConsts.HELP_ICON));
                super.mouseExited(e);
            }
        });
    }
}