package run.halo.photos.service;

import reactor.core.publisher.Mono;
import run.halo.app.extension.ListResult;
import run.halo.app.extension.router.IListRequest.QueryListRequest;
import run.halo.photos.PhotoGroup;

/**
 * @author LIlGG
 * @since 2.0.0
 */
public interface PhotoGroupService {
    Mono<ListResult<PhotoGroup>> listPhotoGroup(QueryListRequest request);
    
    Mono<PhotoGroup> deletePhotoGroup(String name);
    
}
