package hoot.model.mapper.dtoToEntity.hoot;

import hoot.front.api.dto.hoot.ImageDTO;
import hoot.model.entities.Hoot;
import hoot.model.entities.Image;

public class ImageDtoToImageMapper
{
    public Hoot map(ImageDTO imageDTO)
    {
        Image image = new Image();


        image.content = imageDTO.content;
        return new Image();
    }
}
