package hoot.model.mapper.dtoToEntity.hoot;

import hoot.front.api.dto.hoot.ImageDTO;
import hoot.model.entities.Comment;
import hoot.model.entities.Hoot;
import hoot.model.entities.Image;
import hoot.system.ObjectManager.ObjectManager;

public class ImageDtoToImageMapper
{
    public Hoot map(ImageDTO imageDTO)
    {
        Image image = new Image();

        image.imagePath

        image.content = imageDTO.content;
        return new Image();
    }
}
