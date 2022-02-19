package hbv.api.dto.user;

import java.util.ArrayList;

public class UserDTO
{
    public int id;

    public String username;

    public String imageUrl;

    public String imageFilename;

    public String image;

    public ArrayList<FollowerDTO> followers;

    public ArrayList<FollowDTO> follows;
}
