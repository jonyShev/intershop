package com.jonyshev.intershop.mapper;

import com.jonyshev.intershop.dto.ItemDto;
import com.jonyshev.intershop.model.Item;
import com.jonyshev.intershop.service.CartService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    @Mapping(target = "count", expression = "java(cartService.getCountForItem(item.getId()))")
    ItemDto toDto(Item item, CartService cartService);
}
