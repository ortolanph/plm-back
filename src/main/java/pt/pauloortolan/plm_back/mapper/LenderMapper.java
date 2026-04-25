package pt.pauloortolan.plm_back.mapper;

import org.mapstruct.*;
import pt.pauloortolan.plm_back.dto.*;
import pt.pauloortolan.plm_back.model.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LenderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Lender toEntity(CreateLenderRequest request);

    LenderResponse toResponse(Lender entity);
}