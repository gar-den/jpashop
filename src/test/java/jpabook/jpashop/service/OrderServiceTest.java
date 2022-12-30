package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

	@Autowired
	EntityManager em;
	@Autowired
	OrderService orderService;
	@Autowired
	OrderRepository orderRepository;

	@Test
	public void 상품주문() throws Exception {
	    // given
		Member member = createMember("회원1");
		Item book = createBook("책", 10000, 10);

		int orderCount = 2;
	    
	    // when
		Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

		// then
		Order order = orderRepository.findOne(orderId);

		assertEquals("상품 주문시 상태는 ORDER", OrderStatus.ORDER, order.getStatus());
		assertEquals("주문한 상품 종료 수", 1, order.getOrderItems().size());
		assertEquals("총 가격", orderCount * 10000, order.getTotalPrice());
		assertEquals("재고 줄어들기", 10 - orderCount, book.getStockQuantity());
	}

	@Test()
	public void 상품주문_재고수량초과취소() throws Exception {
	    // given
		Member member = createMember("회원1");
		Item book = createBook("책1", 10000, 10);
	    
	    // when
		try {
			orderService.order(member.getId(), book.getId(), 11);
		} catch (NotEnoughStockException e) {
			return;
		}
	    
	    
	    // then
		fail("재고 수량 부족 예외가 발생해야 한다.");
	}
	
	@Test
	public void 주문취소() throws Exception {
	    // given
		Member member = createMember("회원1");
		Item book = createBook("책1", 10000, 10);

		Long orderId = orderService.order(member.getId(), book.getId(), 2);
	    
	    // when
		orderService.cancelOrder(orderId);
	    
	    // then
		Order order = orderRepository.findOne(orderId);

		assertEquals("주문 취소시 상태는 CANCEL", OrderStatus.CANCEL, order.getStatus());
		assertEquals("재고 복구", 10, book.getStockQuantity());
	}

	private Member createMember(String name) {
		Member member = new Member();
		member.setName(name);
		member.setAddress(new Address("서울", "강가", "123-123"));
		em.persist(member);

		return member;
	}

	private Item createBook(String name, int price, int stockQuantity) {
		Item book = new Book();
		book.setName(name);
		book.setPrice(price);
		book.setStockQuantity(stockQuantity);
		em.persist(book);

		return book;
	}
}