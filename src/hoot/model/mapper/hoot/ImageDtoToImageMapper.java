package hoot.model.mapper.hoot;

import hoot.front.api.dto.hoot.ImageDTO;
import hoot.model.entities.Hoot;
import hoot.model.entities.Image;

public class ImageDtoToImageMapper
{
    public Hoot map(ImageDTO hootDTO)
    {
        // TODO: Mapping
        return new Image();
    }
}
