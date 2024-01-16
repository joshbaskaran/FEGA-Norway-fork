package no.elixir.tsdapimock.files;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumablesRepository extends CrudRepository<ResumableUpload, String> {}
