package hoot.front.api.dto.hoot;

import java.time.LocalDateTime;

public class PostDTO
{
    public Integer id;

    public HootType type;

    public LocalDateTime created;

    public LocalDateTime modified;

    public String content;
}
