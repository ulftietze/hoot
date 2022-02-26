package hoot.front.api.dto.hoot;

import java.util.ArrayList;

public class HootsDTO
{
    public ArrayList<HootDTO> hoots = new ArrayList<>();

    public void addHoot(HootDTO hootDTO)
    {
        this.hoots.add(hootDTO);
    }
}
