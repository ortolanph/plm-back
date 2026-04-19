package pt.pauloortolan.plm_back.mapper;

import org.mapstruct.MapMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import pt.pauloortolan.plm_back.dto.CreateLenderRequest;
import pt.pauloortolan.plm_back.dto.LenderResponse;
import pt.pauloortolan.plm_back.dto.UpdateLenderRequest;
import pt.pauloortolan.plm_back.model.Lender;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LenderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Lender toEntity(CreateLenderRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Lender toEntityFromUpdate(UpdateLenderRequest request);

    LenderResponse toResponse(Lender entity);
}