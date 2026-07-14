package com.edu.course.service;

import com.edu.common.entity.Message;
import com.edu.course.mapper.MessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;

    public Message send(Message message) {
        message.setIsRead(0);
        messageMapper.insert(message);
        return message;
    }

    public List<Message> listConversations(Long userId) {
        List<Long> contactIds = messageMapper.selectContactIds(userId);
        List<Message> result = new ArrayList<>();
        Set<Long> seen = new HashSet<>();
        for (Long contactId : contactIds) {
            if (seen.contains(contactId)) continue;
            seen.add(contactId);
            List<Message> msgs = messageMapper.selectConversation(userId, contactId);
            if (!msgs.isEmpty()) {
                result.add(msgs.get(msgs.size() - 1));
            }
        }
        result.sort((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()));
        return result;
    }

    public List<Message> getConversation(Long userId, Long otherId) {
        messageMapper.markRead(userId, otherId);
        return messageMapper.selectConversation(userId, otherId);
    }

    public long countUnread(Long userId) {
        return messageMapper.countUnread(userId);
    }
}
