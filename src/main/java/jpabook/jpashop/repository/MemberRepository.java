package jpabook.jpashop.repository;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

@Repository  // String bean 으로 등록해 줌
@RequiredArgsConstructor
public class MemberRepository {

	private final EntityManager em;  // 원래 @PersistenceContext 으로 injection 바당야 하는데 spring이 Autowired로도 받을 수 있게 지원해 줌

	public void save(Member member) {
		em.persist(member);
	}

	public Member findOne(Long id) {
		return em.find(Member.class, id);
	}

	public List<Member> findAll() {
		return em.createQuery("select m from Member m", Member.class)  // 쿼리의 대상이 테이블이 아니라 엔티티
				.getResultList();
	}

	public List<Member> findByName(String name) {
		return em.createQuery("select m from Member m where m.name = :name", Member.class)
				.setParameter("name", name)
				.getResultList();
	}
}
