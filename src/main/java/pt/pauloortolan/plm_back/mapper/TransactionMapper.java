package pt.pauloortolan.plm_back.mapper;

import org.mapstruct.*;
import pt.pauloortolan.plm_back.dto.*;
import pt.pauloortolan.plm_back.model.*;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "lenderId", source = "lender.id")
    TransactionResponse toResponse(Transaction transaction);
}