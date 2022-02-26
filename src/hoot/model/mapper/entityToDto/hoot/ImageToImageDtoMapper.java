package hoot.model.mapper.entityToDto.hoot;

import hoot.front.api.dto.hoot.ImageDTO;
import hoot.model.entities.Image;

public class ImageToImageDtoMapper
{
    public ImageDTO map(Image image)
    {
        ImageDTO dto = new ImageDTO();
        dto.imageUrl = image.imagePath; // TODO: Load URL
        dto.content  = image.content;

        return dto;
    }
}
