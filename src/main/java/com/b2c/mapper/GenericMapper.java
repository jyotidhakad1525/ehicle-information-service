package com.b2c.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface GenericMapper<I, O> {
    O convertFromDTOToEntity(I paramI) throws JsonProcessingException;

    I convertFromEntityToDTO(O paramO);
}