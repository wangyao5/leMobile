package com.letv.emm.repository;

import com.letv.emm.entity.PushPropEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PushPropRepository extends JpaRepository<PushPropEntity, String> {
    @Modifying(clearAutomatically = true)
    @Query("update PushPropEntity pushPropEntity set pushPropEntity.communityNo =:communityNo, pushPropEntity.pub = :pub, pushPropEntity.pubk = :pubk, pushPropEntity.appkey = :appkey where pushPropEntity.appId =:appId")
    void updatePushProp(@Param("appId") String appId, @Param("communityNo") String communityNo, @Param("pub") String pub, @Param("pubk") String pubk, @Param("appkey") String appkey);

    List<PushPropEntity> findByAppkey(String appKey);
}
