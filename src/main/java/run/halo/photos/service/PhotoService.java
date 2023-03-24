package run.halo.photos.service;

import reactor.core.publisher.Mono;
import run.halo.app.extension.ListResult;
import run.halo.photos.Photo;
import run.halo.photos.PhotoQuery;
import run.halo.photos.PhotoRequest;

/**
 * @author LIlGG
 * @since 2.0.0
 */
public interface PhotoService {
    Mono<ListResult<Photo>> listPhoto(PhotoQuery query);
}
