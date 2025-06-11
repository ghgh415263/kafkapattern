package com.example.kafkapattern.order.event;

import java.math.BigDecimal;

/**
 * 결제 서비스에 결제 처리를 요청하는 커맨드 이벤트입니다.
 *
 * @param orderId     결제를 요청하는 주문ID
 * @param totalAmount 결제할 총 금액 (BigDecimal)
 */
public record PaymentProcessCommand(String orderId, BigDecimal totalAmount) {
}