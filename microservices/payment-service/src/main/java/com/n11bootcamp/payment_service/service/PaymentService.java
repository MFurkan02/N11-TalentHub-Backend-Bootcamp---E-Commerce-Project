package com.n11bootcamp.payment_service.service;

import com.iyzipay.Options;
import com.iyzipay.model.*;
import com.iyzipay.request.CreatePaymentRequest;
import com.n11bootcamp.payment_service.dto.PaymentRequest;
import com.n11bootcamp.payment_service.dto.PaymentResponse;
import com.n11bootcamp.payment_service.entity.PaymentTransaction;
import com.n11bootcamp.payment_service.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    private final Options options;
    private final PaymentRepository paymentRepository;

    public PaymentService(Options options, PaymentRepository paymentRepository) {
        this.options = options;
        this.paymentRepository = paymentRepository;
    }

    public PaymentResponse processPayment(PaymentRequest request) {
        CreatePaymentRequest iyzicoRequest = prepareIyzicoRequest(request);

        // Iyzico API Çağrısı
        Payment payment = Payment.create(iyzicoRequest, options);

        PaymentResponse response = new PaymentResponse();
        boolean isSuccess = "success".equals(payment.getStatus());

        response.setSuccess(isSuccess);
        response.setTransactionId(isSuccess ? payment.getPaymentId() : null);
        response.setMessage(isSuccess ? "Başarılı" : payment.getErrorMessage());

        // Sonucu Veritabanına Kaydet
        saveTransaction(request, response);

        return response;
    }

    private CreatePaymentRequest prepareIyzicoRequest(PaymentRequest request) {
        CreatePaymentRequest iyzicoRequest = new CreatePaymentRequest();
        iyzicoRequest.setPrice(new BigDecimal(request.getAmount()));
        iyzicoRequest.setPaidPrice(new BigDecimal(request.getAmount()));
        iyzicoRequest.setCurrency(Currency.TRY.name());
        iyzicoRequest.setInstallment(1);
        iyzicoRequest.setBasketId("ORD-" + request.getOrderId());
        iyzicoRequest.setPaymentChannel(PaymentChannel.WEB.name());
        iyzicoRequest.setConversationId(UUID.randomUUID().toString());

        // Kart
        PaymentCard paymentCard = new PaymentCard();
        paymentCard.setCardHolderName(request.getCard().getCardHolderName());
        paymentCard.setCardNumber(request.getCard().getCardNumber());
        paymentCard.setExpireMonth(request.getCard().getExpireMonth());
        paymentCard.setExpireYear(request.getCard().getExpireYear());
        paymentCard.setCvc(request.getCard().getCvc());
        iyzicoRequest.setPaymentCard(paymentCard);

        // Buyer
        Buyer buyer = new Buyer();
        buyer.setId(request.getUsername());
        buyer.setName(request.getFirstName());
        buyer.setSurname(request.getLastName());
        buyer.setEmail(request.getEmail());
        buyer.setGsmNumber(request.getPhone());
        buyer.setIdentityNumber("11111111111"); // Sandbox için zorunlu
        buyer.setRegistrationAddress(request.getStreetAddress());
        buyer.setCity(request.getCity() != null ? request.getCity() : "Istanbul");
        buyer.setCountry(request.getCountry() !=null ? request.getCountry() : "Türkiye");
        iyzicoRequest.setBuyer(buyer);

        // Address
        Address billingAddress = new Address();
        billingAddress.setContactName(request.getFirstName() + " " + request.getLastName());
        billingAddress.setCity(request.getCity() != null ? request.getCity() : "Istanbul"); // Burası kritik!
        billingAddress.setCountry(request.getCountry() !=null ? request.getCountry() : "Türkiye");
        billingAddress.setAddress(request.getStreetAddress());
        iyzicoRequest.setBillingAddress(billingAddress);
        iyzicoRequest.setShippingAddress(billingAddress);

        // Items
        List<BasketItem> basketItems = new ArrayList<>();
        request.getItems().forEach(item -> {
            BasketItem basketItem = new BasketItem();
            basketItem.setId(item.getProductId().toString());
            basketItem.setName(item.getProductName());
            basketItem.setCategory1(item.getCategory1() != null ? item.getCategory1() : "Electronics");
            basketItem.setItemType(BasketItemType.PHYSICAL.name());
            basketItem.setPrice(new BigDecimal(item.getPrice() * item.getQuantity()));
            basketItems.add(basketItem);
        });
        iyzicoRequest.setBasketItems(basketItems);

        return iyzicoRequest;
    }

    private void saveTransaction(PaymentRequest req, PaymentResponse res) {
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setOrderId(req.getOrderId());
        transaction.setUsername(req.getUsername());
        transaction.setAmount(req.getAmount());
        transaction.setTransactionId(res.getTransactionId());
        transaction.setStatus(res.isSuccess());
        paymentRepository.save(transaction);
    }
}