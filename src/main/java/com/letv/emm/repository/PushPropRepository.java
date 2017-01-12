package com.letv.emm.repository;

import com.letv.emm.entity.PushPropEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PushPropRepository extends JpaRepository<PushPropEntity, Long> {

}
