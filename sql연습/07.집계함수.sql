-- 1) 집계쿼리: select 절에 통계함수(count, max, min, sum, avg, variance, stddev, ...)
select avg(salary), sum(salary) from salaries;

-- 2) select 절에 집계함수(그룹함수)가 있는 경우, 어떤 컬럼도 select절에 올 수 없다!!
--    emp_no는 아무 의미가 없다.
--    오류!!!!!
select emp_no, avg(salary) from salaries;

-- 3) query 순서
-- 1. from: 접근 테이블 확인
-- 2. where: 조건에 맞는 row를 선택(임시 테이블)
-- 3. 집계(결과 테이블)
-- 4. projection
-- 예제: 사번이 10060인 사원이 받은 평균 월급
select avg(salary) from salaries where emp_no = '10060';

-- 4) group by에 참여하고 있는 컬럼은 projection이 가능하다.: select 절에 올 수 있다.
-- 예제: 사원별 평균 월급은?
select emp_no, avg(salary) from salaries group by emp_no;

-- 5) having
--    집계결과(결과 테이블)에서 row를 선택해야 하는 경우
--    이미 where절은 실행이 되었기 때문에 having절에 이 조건을 주어야 한다.
-- 예제: 평균월급이 60000 달러 이상인 사원의 사번과 평균월급을 출력하세요.
select emp_no, avg(salary) as avg_salary from salaries group by emp_no having avg_salary >= 60000;

-- 6) order by
--    order by는 항상 맨 마지막 출력(projection) 전에 한다.
select emp_no, avg(salary) as avg_salary from salaries group by emp_no having avg_salary >= 60000 order by avg_salary asc;

-- 주의) 사번이 10060인 사번의 사번, 평균 월급, 급여총합을 출력하세요.
-- 문법적으로 오류
-- 의미적으로 맞다(where 때문에)
select emp_no, avg(salary), sum(salary) from salaries where emp_no = 10060;

-- 문법적으로 옳다.
select emp_no, avg(salary), sum(salary) from salaries group by emp_no having emp_no = '10060';
