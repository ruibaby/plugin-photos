package run.halo.photos.service.impl;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ListResult;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.extension.router.IListRequest.QueryListRequest;
import run.halo.photos.Photo;
import run.halo.photos.PhotoGroup;
import run.halo.photos.service.PhotoGroupService;

import java.util.function.Function;
import java.util.function.Predicate;

import static run.halo.app.extension.router.selector.SelectorUtil.labelAndFieldSelectorToPredicate;

/**
 * Service implementation for {@link Photo}.
 *
 * @author LIlGG
 * @since 1.0.0
 */
@Component
public class PhotoGroupServiceImpl implements PhotoGroupService {

    private final ReactiveExtensionClient client;

    public PhotoGroupServiceImpl(ReactiveExtensionClient client) {
        this.client = client;
    }

    @Override
    public Mono<ListResult<PhotoGroup>> listPhotoGroup(QueryListRequest query) {
        return this.client.list(PhotoGroup.class, photoListPredicate(query),
                null, query.getPage(), query.getSize())
            .flatMap(listResult -> Flux.fromStream(
                        listResult.get().map(this::populatePhotos)
                    )
                    .concatMap(Function.identity())
                    .collectList()
                    .map(groups -> new ListResult<>(listResult.getPage(), listResult.getSize(),
                        listResult.getTotal(), groups)
                    )
            );
    }

    private Mono<PhotoGroup> populatePhotos(PhotoGroup photoGroup) {
        return Mono.just(photoGroup)
                .flatMap(fg -> fetchPhotoCount(fg)
                    .doOnNext(count -> fg.getStatusOrDefault().setPhotoCount(count))
                    .thenReturn(fg)
                );
    }

    Mono<Integer> fetchPhotoCount(PhotoGroup photoGroup) {
        Assert.notNull(photoGroup, "The photoGroup must not be null.");
        String name = photoGroup.getMetadata().getName();
        return client.list(Photo.class,
            photo -> !photo.isDeleted() && photo.getSpec().getGroupName().equals(name), null)
            .count()
            .defaultIfEmpty(0L)
            .map(Long::intValue);
    }

    Predicate<PhotoGroup> photoListPredicate(QueryListRequest query) {
        return labelAndFieldSelectorToPredicate(query.getLabelSelector(), query.getFieldSelector());
    }
}
