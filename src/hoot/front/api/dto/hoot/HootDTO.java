package hoot.front.api.dto.hoot;

import hoot.front.api.dto.user.UserDTO;

import java.time.LocalDateTime;

public abstract class HootDTO
{
    public Integer id;

    public HootType type;

    public LocalDateTime created;

    public UserDTO user;
}
