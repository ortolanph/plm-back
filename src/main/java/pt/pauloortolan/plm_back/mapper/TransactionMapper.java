package pt.pauloortolan.plm_back.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pt.pauloortolan.plm_back.dto.TransactionResponse;
import pt.pauloortolan.plm_back.model.Transaction;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "lenderId", source = "lender.id")
    TransactionResponse toResponse(Transaction transaction);
}