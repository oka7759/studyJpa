package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("설명")
   public void 상품주문() throws Exception {
        // given
        Member member = createMember("회원1");

        Book book = createBook("jpa", 10000, 10);

        int orderCount = 2;


        // when

        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        // then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals("상품주문시 상태 order", OrderStatus.ORDER,getOrder.getStatus());
        assertEquals("주문한 상품 종류 수가", 1,getOrder.getOrderItems().size());
        assertEquals("주문가격", 10000 * orderCount,getOrder.getTotalPrice());
        assertEquals("재고 확인", 8,book.getStockQuantity());

    }



    @Test
    @DisplayName("설명")
    public void 주문취소() throws Exception {
        // given
        Member member = createMember("회원1");
        Book book = createBook("jpa", 10000, 10);
        Long orderId = orderService.order(member.getId(), book.getId(), 10);
        // when
        orderService.cancel(orderId);
        Order order = orderRepository.findOne(orderId);
        // then
        assertEquals("상태확인",OrderStatus.CANCEL, order.getStatus());
        assertEquals("수량 복구", book.getStockQuantity(),10);
    }

    @Test(expected = NotEnoughStockException.class)
    @DisplayName("설명")
    public void 재고수량_초과() throws Exception {
        // given
        Member member = createMember("회원1");
        Book book = createBook("jpa", 10000, 10);
        int orderCount = 11;
        // when
        orderService.order(member.getId(), book.getId(), orderCount);

        // then
        fail("재고 수량은 예외가 발생해야 한다");
    }




    private Book createBook(String name, int price, int quantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(quantity);
        em.persist(book);
        return book;
    }

    private Member createMember(String name) {
        Member member = new Member();
        member.setName(name);
        member.setAddress(new Address("서울", "고현로", "1111"));
        em.persist(member);
        return member;
    }
}