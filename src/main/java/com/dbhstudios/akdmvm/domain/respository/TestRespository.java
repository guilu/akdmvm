package com.dbhstudios.akdmvm.domain.respository;

import com.dbhstudios.akdmvm.domain.entity.auth.User;
import com.dbhstudios.akdmvm.domain.entity.model.Test;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestRespository extends PagingAndSortingRepository<Test, Long> {

    List<Test> findByFinalizadoTrueAndUser(User user);


    @Query("SELECT (SUM(t.aciertos)*1.0/SUM(t.numeroPreguntasTotal))*100.0 FROM Test t where t.user=?1 group by t.user")
    Double findAvgAciertosByUser(User user);

    @Query("SELECT (SUM(t.fallos)*1.0/SUM(t.numeroPreguntasTotal))*100.0 FROM Test t where t.user=?1 group by t.user")
    Double findAvgFallosByUser(User user);

}
