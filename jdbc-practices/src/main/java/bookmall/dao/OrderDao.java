package bookmall.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bookmall.vo.OrderBookVo;
import bookmall.vo.OrderVo;

public class OrderDao {
	
	private Connection getConnection() throws SQLException {
		Connection conn = null;
		
		try {
			Class.forName("org.mariadb.jdbc.Driver");
			
			String url = "jdbc:mariadb://192.168.64.3:3306/bookmall?charset=utf8";
			conn = DriverManager.getConnection(url, "bookmall", "bookmall");
		} catch (ClassNotFoundException e) {
			System.out.println("드라이버 로딩 실패: " + e);
		}
		
		return conn;
	}

	public void insert(OrderVo mockOrderVo) {
		try (
				Connection conn = getConnection();
				PreparedStatement pstmt1 = conn.prepareStatement("insert into orders(user_no, number, payment, shipping, status) values(?, ?, ?, ?, ?)");
				PreparedStatement pstmt2 = conn.prepareStatement("select last_insert_id() from dual");
		) {
			pstmt1.setLong(1, mockOrderVo.getUserNo());
			pstmt1.setString(2, mockOrderVo.getNumber());
			pstmt1.setLong(3, mockOrderVo.getPayment());
			pstmt1.setString(4, mockOrderVo.getShipping());
			pstmt1.setString(5, mockOrderVo.getStatus());
			pstmt1.executeUpdate();
		
			ResultSet rs = pstmt2.executeQuery();
			mockOrderVo.setNo(rs.next() ? rs.getLong(1) : null);
			rs.close();
		} catch (SQLException e) {
			System.out.println("error: " + e);
		}
	}

	public void insertBook(OrderBookVo mockOrderBookVo) {		
		try (
				Connection conn = getConnection();
				PreparedStatement pstmt = conn.prepareStatement("insert into orders_book(orders_no, book_no, quantity, price) values(?, ?, ?, ?)");
		) {
			pstmt.setLong(1, mockOrderBookVo.getOrderNo());
			pstmt.setLong(2, mockOrderBookVo.getBookNo());
			pstmt.setLong(3, mockOrderBookVo.getQuantity());
			pstmt.setLong(4, mockOrderBookVo.getPrice());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("error: " + e);
		}
	}

	public OrderVo findByNoAndUserNo(Long no, Long userNo) {
		OrderVo orderVo = null;
		
		try (
				Connection conn = getConnection();
				PreparedStatement pstmt = conn.prepareStatement("select no, number, user_no, status, payment, shipping from orders where no = ? and user_no = ?");
		) {
			pstmt.setLong(1, no);
			pstmt.setLong(2, userNo);
			
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) {				
				Long orders_no = rs.getLong(1);
				String number = rs.getString(2);
				Long user_no = rs.getLong(3);
				String status = rs.getString(4);
				int payment = rs.getInt(5);
				String shipping = rs.getString(6);
				
				orderVo = new OrderVo();
				orderVo.setNo(orders_no);
				orderVo.setNumber(number);
				orderVo.setUserNo(user_no);
				orderVo.setStatus(status);
				orderVo.setPayment(payment);
				orderVo.setShipping(shipping);
			}
			rs.close();
		} catch (SQLException e) {
			System.out.println("error: " + e);
		}
		
		return orderVo;
	}
	
	public List<OrderBookVo> findBooksByNoAndUserNo(Long no, Long userNo) {
		List<OrderBookVo> result = new ArrayList<>();
		
		try (
				Connection conn = getConnection();
				PreparedStatement pstmt = conn.prepareStatement("select orders_no, book_no, quantity, orders_book.price, title from orders_book, orders, book, user "
																+ "where orders_book.orders_no = orders.no and orders_book.book_no = book.no and orders.user_no = user.no and "
																+ "orders.no = ? and  user.no = ?");
		) {
			pstmt.setLong(1, no);
			pstmt.setLong(2, userNo);
			
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Long orders_no = rs.getLong(1);
				Long book_no = rs.getLong(2);
				int quantity = rs.getInt(3);
				int price = rs.getInt(4);
				String title = rs.getString(5);
				
				OrderBookVo orderBookVo = new OrderBookVo();
				orderBookVo.setOrderNo(orders_no);
				orderBookVo.setBookNo(book_no);
				orderBookVo.setQuantity(quantity);
				orderBookVo.setPrice(price);
				orderBookVo.setBookTitle(title);
				
				result.add(orderBookVo);
			}
			rs.close();
		} catch (SQLException e) {
			System.out.println("error: " + e);
		}
		
		return result;
	}
	
	public void deleteBooksByNo(Long no) {
		try (
				Connection conn = getConnection();
				PreparedStatement pstmt = conn.prepareStatement("delete from orders_book where orders_no = ?");
		) {
			pstmt.setLong(1, no);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("error: " + e);
		}
	}

	public void deleteByNo(Long no) {
		try (
				Connection conn = getConnection();
				PreparedStatement pstmt = conn.prepareStatement("delete from orders where no = ?");
		) {
			pstmt.setLong(1, no);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("error: " + e);
		}
	}
}
