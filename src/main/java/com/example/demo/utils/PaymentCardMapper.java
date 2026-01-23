package com.example.demo.utils;

import com.example.demo.dto.PaymentCardCreateDto;
import com.example.demo.dto.PaymentCardResponseDto;
import com.example.demo.model.PaymentCard;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentCardMapper {

    PaymentCard toEntity(PaymentCardCreateDto dto);

    PaymentCardResponseDto toDto(PaymentCard card);
}
