package hoot.initialisation.Factory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import hoot.model.entities.Comment;
import hoot.model.entities.Hoot;
import hoot.model.entities.Image;
import hoot.model.entities.Post;
import hoot.system.ObjectManager.FactoryInterface;
import hoot.system.ObjectManager.ObjectManager;
import hoot.system.Serializer.LocalDateTimeSerializer;

import java.time.LocalDateTime;

public class GsonSerializerFactory implements FactoryInterface<Gson>
{
    @Override
    public Gson create()
    {
        GsonBuilder builder = (GsonBuilder) ObjectManager.get(GsonBuilder.class);

        this.createTypeAdapterFactoryForHoot(builder);
        this.createTypeAdapterFactoryForLocalDateTime(builder);

        return builder.setPrettyPrinting().serializeNulls().create();
    }

    private void createTypeAdapterFactoryForLocalDateTime(GsonBuilder builder)
    {
        LocalDateTimeSerializer serializer = (LocalDateTimeSerializer) ObjectManager.get(LocalDateTimeSerializer.class);

        builder.registerTypeAdapter(LocalDateTime.class, serializer);
    }

    private void createTypeAdapterFactoryForHoot(GsonBuilder builder)
    {
        TypeAdapterFactory factory = RuntimeTypeAdapterFactory
                .of(Hoot.class, "type")
                .registerSubtype(Post.class)
                .registerSubtype(Comment.class)
                .registerSubtype(Image.class);

        builder.registerTypeAdapterFactory(factory);
    }
}
