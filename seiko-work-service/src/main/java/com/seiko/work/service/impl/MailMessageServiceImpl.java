package com.seiko.work.service.impl;

import com.seiko.work.base.ResultCode;
import com.seiko.work.entity.MailAccount;
import com.seiko.work.entity.MailMessage;
import com.seiko.work.exception.BusinessException;
import com.seiko.work.service.MailAccountService;
import com.seiko.work.service.MailMessageService;
import org.eclipse.angus.mail.imap.IMAPFolder;
import jakarta.mail.Address;
import jakarta.mail.BodyPart;
import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.internet.InternetAddress;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

/**
 * 邮件 Service 实现（通过 IMAP 实时获取，不持久化）
 */
@Service
@RequiredArgsConstructor
public class MailMessageServiceImpl implements MailMessageService {

    private static final String INBOX = "INBOX";

    private final MailAccountService mailAccountService;

    @Override
    public List<MailMessage> listAll(Long userId) {
        MailAccount account = requireAccount(userId);
        try (Store store = connect(account); Folder folder = store.getFolder(INBOX)) {
            folder.open(Folder.READ_ONLY);
            Message[] messages = folder.getMessages();
            List<MailMessage> list = new ArrayList<>(messages.length);
            for (Message message : messages) {
                list.add(parseMessage(message, false));
            }
            list.sort(Comparator.comparing(MailMessage::getReceiveTime,
                    Comparator.nullsLast(Comparator.naturalOrder())).reversed());
            return list;
        } catch (MessagingException | IOException e) {
            throw new BusinessException("获取邮件失败：" + e.getMessage());
        }
    }

    @Override
    public MailMessage getDetail(Long userId, String messageUid) {
        MailAccount account = requireAccount(userId);
        try (Store store = connect(account); Folder folder = store.getFolder(INBOX)) {
            folder.open(Folder.READ_ONLY);
            Message message = getMessageByUid(folder, messageUid);
            if (message == null) {
                throw new BusinessException(ResultCode.NOT_FOUND);
            }
            return parseMessage(message, true);
        } catch (MessagingException | IOException e) {
            throw new BusinessException("获取邮件详情失败：" + e.getMessage());
        }
    }

    @Override
    public void markRead(Long userId, String messageUid) {
        MailAccount account = requireAccount(userId);
        try (Store store = connect(account); Folder folder = store.getFolder(INBOX)) {
            folder.open(Folder.READ_WRITE);
            Message message = getMessageByUid(folder, messageUid);
            if (message == null) {
                throw new BusinessException(ResultCode.NOT_FOUND);
            }
            message.setFlags(new Flags(Flags.Flag.SEEN), true);
        } catch (MessagingException e) {
            throw new BusinessException("标记已读失败：" + e.getMessage());
        }
    }

    private MailAccount requireAccount(Long userId) {
        MailAccount account = mailAccountService.getByUserId(userId);
        if (account == null) {
            throw new BusinessException("未配置邮箱账号，请先保存邮箱授权信息");
        }
        return account;
    }

    private Store connect(MailAccount account) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.store.protocol", "imap");
        props.put("mail.imap.host", account.getImapHost());
        props.put("mail.imap.port", String.valueOf(account.getImapPort()));
        props.put("mail.imap.ssl.enable", String.valueOf(!Boolean.FALSE.equals(account.getSslEnable())));
        // 读取邮件时不自动设置已读标记
        props.put("mail.imap.peek", "true");
        Session session = Session.getInstance(props);
        Store store = session.getStore("imap");
        store.connect(account.getImapHost(), account.getImapPort(), account.getEmail(), account.getAuthCode());
        return store;
    }

    private Message getMessageByUid(Folder folder, String messageUid) throws MessagingException {
        return ((IMAPFolder) folder).getMessageByUID(Long.parseLong(messageUid));
    }

    private MailMessage parseMessage(Message message, boolean withContent) throws MessagingException, IOException {
        MailMessage mail = new MailMessage();
        if (message.getFolder() instanceof IMAPFolder imapFolder) {
            mail.setMessageUid(String.valueOf(imapFolder.getUID(message)));
        }
        Address[] from = message.getFrom();
        if (from != null && from.length > 0 && from[0] instanceof InternetAddress address) {
            mail.setFromAddress(address.getAddress());
            mail.setFromName(address.getPersonal());
        }
        mail.setSubject(message.getSubject());
        mail.setReceiveTime(message.getReceivedDate() != null
                ? message.getReceivedDate() : message.getSentDate());
        mail.setIsRead(message.isSet(Flags.Flag.SEEN));
        if (withContent) {
            parseContent(message, mail);
        } else {
            mail.setHasAttachment(hasAttachment(message));
        }
        return mail;
    }

    /**
     * 递归解析正文内容，text/plain 优先作为纯文本正文，text/html 保存原始HTML
     */
    private void parseContent(Part part, MailMessage mail) throws MessagingException, IOException {
        if (part.isMimeType("text/plain") && mail.getContentText() == null) {
            mail.setContentText((String) part.getContent());
            return;
        }
        if (part.isMimeType("text/html") && mail.getContentHtml() == null) {
            mail.setContentHtml((String) part.getContent());
            return;
        }
        if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                if (isAttachment(bodyPart)) {
                    mail.setHasAttachment(true);
                }
                parseContent(bodyPart, mail);
            }
        }
    }

    private boolean hasAttachment(Part part) throws MessagingException, IOException {
        if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            for (int i = 0; i < multipart.getCount(); i++) {
                if (hasAttachment(multipart.getBodyPart(i))) {
                    return true;
                }
            }
            return false;
        }
        return isAttachment(part);
    }

    private boolean isAttachment(Part part) throws MessagingException {
        return part.getFileName() != null
                || Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition());
    }

}
