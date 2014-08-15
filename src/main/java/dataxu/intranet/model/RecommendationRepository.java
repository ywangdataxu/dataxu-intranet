package dataxu.intranet.model;

import java.util.List;

public interface RecommendationRepository {

    public List<Recommendation> findAll();
}
