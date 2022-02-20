package hoot.front.api.dto.hoot;

import java.time.LocalDateTime;

public class ImageDTO
{
    public Integer id;

    public HootType type;

    public LocalDateTime created;

    public LocalDateTime modified;

    public String content;

    public String imageUrl;

    public String imageFilename;

    public String image;
}
