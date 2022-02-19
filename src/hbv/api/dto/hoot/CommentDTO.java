package hbv.api.dto.hoot;

import java.time.LocalDateTime;

public class CommentDTO
{
    public Integer id;

    public HootType type;

    public LocalDateTime created;

    public LocalDateTime modified;

    public HootDTO parent;

    public String content;
}
