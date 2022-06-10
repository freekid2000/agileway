package com.jn.agileway.dmmq.core;

import com.lmax.disruptor.EventTranslator;

/**
 * 多个topic不能共用同一个translator
 * <p>
 * 一个topic 一个，每一次 publish时，要保证 translator 是独占使用的，因为 publish动作是阻塞。
 *
 * @param <M>
 */
public interface MessageTranslator<M> extends EventTranslator<MessageHolder<M>>, TopicNameAware {
    void setMessage(M message);

    M getMessage();

    @Override
    void translateTo(MessageHolder<M> event, long sequence);

    boolean isIdle();
}
