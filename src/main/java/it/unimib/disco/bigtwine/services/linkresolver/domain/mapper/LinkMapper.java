package it.unimib.disco.bigtwine.services.linkresolver.domain.mapper;

import it.unimib.disco.bigtwine.commons.messaging.dto.LinkDTO;
import it.unimib.disco.bigtwine.commons.messaging.dto.ResourceDTO;
import it.unimib.disco.bigtwine.services.linkresolver.domain.Link;
import it.unimib.disco.bigtwine.services.linkresolver.domain.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LinkMapper {
    LinkMapper INSTANCE = Mappers.getMapper( LinkMapper.class );

    Link[] linksFromDTOs(LinkDTO[] linkDTOS);
    ResourceDTO[] dtosFromResources(Resource[] resources);
}
