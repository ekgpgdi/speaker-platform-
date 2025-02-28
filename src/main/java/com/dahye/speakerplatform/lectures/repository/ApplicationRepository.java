package com.dahye.speakerplatform.lectures.repository;

import com.dahye.speakerplatform.lectures.domain.Application;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long>, ApplicationRepositoryCustom {

}
