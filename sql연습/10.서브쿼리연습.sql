--
-- subquery
--

--
-- 1) select 절, insert into t1 values(...)
--
select (select 1+2 from dual) from dual;
select a.* from (select 1+2 from dual) a;

--
-- 2) from 절의 서브쿼리
--
select now() as n, sysdate() as s, 3 + 1 as r from dual;
select n, s from (select now() as n, sysdate() as s, 3 + 1 as r from dual) a;

--
-- 3) where 절의 서브쿼리
--

-- 예제: 햔재, Fai Bale이 근무하는 부서에서 근무하는 다른 직원의 사번과 이름을 출력하세요.
select b.dept_no
from employees a, dept_emp b
where a.emp_no = b.emp_no and b.to_date = '9999-01-01' and concat(a.first_name, ' ', a.last_name) = 'Fai Bale';
-- 'd004'

select a.emp_no, a.first_name
from employees a, dept_emp b
where a.emp_no = b.emp_no and b.to_date = '9999-01-01' and b.dept_no = 'd004';

select a.emp_no, a.first_name
from employees a, dept_emp b
where a.emp_no = b.emp_no
	and b.to_date = '9999-01-01'
    and b.dept_no = (
		select b.dept_no
		from employees a, dept_emp b
		where a.emp_no = b.emp_no and b.to_date = '9999-01-01' and concat(a.first_name, ' ', a.last_name) = 'Fai Bale'
);

-- 3-1) 단일행 연산자: =, > , <, >=, <=, <>, !=
-- 실습문제1: 현재 전체 사원의 평균 연봉보다 적은 급여를 받는 사원의 이름과 급여를 출력하세요.
select avg(salary) from salaries where to_date = '9999-01-01';

select a.first_name, b.salary
from employees a, salaries b
where a.emp_no = b.emp_no
	and b.to_date = '9999-01-01'
	and b.salary < (
		select avg(salary) from salaries where to_date = '9999-01-01'
	)
order by b.salary desc;

-- 실습문제2: 현재 직책별 평균급여 중에 가장 작은 직책의 직책이름과 그 평균급여를 출력해보세요.
-- ver.유진
select t.title, avg(s.salary)
from titles t, salaries s
where t.emp_no = s.emp_no
	and t.to_date = '9999-01-01'
    and s.to_date = '9999-01-01'
group by t.title
order by avg(s.salary) asc
limit 1;

-- ver.강사님
-- 1) 직책별 평균 급여
select a.title, avg(salary)
from titles a, salaries b
where a.emp_no = b.emp_no
	and t.to_date = '9999-01-01'
    and s.to_date = '9999-01-01'
group by a.title;

-- 2) 직책별 가장 적은 평균 급여: from절 subquery
select min(avg_salary)
from (
	select a.title, avg(salary) as avg_salary
	from titles a, salaries b
	where a.emp_no = b.emp_no
		and t.to_date = '9999-01-01'
		and s.to_date = '9999-01-01'
	group by a.title
) a;

-- 3) sol1: where절 subquery(=)
select a.title, avg(salary)
from titles a, salaries b
where a.emp_no = b.emp_no
	and t.to_date = '9999-01-01'
    and s.to_date = '9999-01-01'
group by a.title
having avg(salary) = (
	select min(avg_salary)
	from (
		select a.title, avg(salary) as avg_salary
		from titles a, salaries b
		where a.emp_no = b.emp_no
			and t.to_date = '9999-01-01'
			and s.to_date = '9999-01-01'
		group by a.title
	) a
);

-- 4) sol2: top-k(limit)
select t.title, avg(s.salary)
from titles t, salaries s
where t.emp_no = s.emp_no
	and t.to_date = '9999-01-01'
    and s.to_date = '9999-01-01'
group by t.title
order by avg(s.salary) asc
limit 0, 1;

-- 3-2) 복수행 연산자: in, not in, 비교연산자any, 비교연산자all <all

-- any 사용법
-- 1. =any: in
-- 2. >any >=any: 최소값
-- 3. <any <=any: 최대값
-- 4. <>any, !=any: not in

-- all 사용법
-- 1. =all: (x)
-- 2. >all, >=all: 최대값
-- 3. <all, <=all: 최소값
-- 4. <>all, !=all

-- 실습문제3: 현재 급여가 50000 이상인 직원의 이름과 급여를 출력하세요.
-- sol1) join
select a.first_name, b.salary
from employees a, salaries b
where a.emp_no = b.emp_no
	and b.to_date = '9999-01-01'
    and b.salary >= 50000
order by b.salary asc;

-- sol2) subquery: where(in)
select emp_no, salary
from salaries
where to_date = '9999-01-01' and salary >= 50000;

select a.first_name, b.salary
from employees a, salaries b
where a.emp_no = b.emp_no
	and b.to_date = '9999-01-01'
    and (a.emp_no, b.salary) in (
		select emp_no, salary
		from salaries
		where to_date = '9999-01-01' and salary >= 50000
)
order by b.salary asc;

-- sol3) subquery: where(=any)
select emp_no, salary
from salaries
where to_date = '9999-01-01' and salary >= 50000;

select a.first_name, b.salary
from employees a, salaries b
where a.emp_no = b.emp_no
	and b.to_date = '9999-01-01'
    and (a.emp_no, b.salary) =any (
		select emp_no, salary
		from salaries
		where to_date = '9999-01-01' and salary >= 50000
)
order by b.salary asc;

-- 실습문제4: 현재 각 부서별로 최고급여를 받고 있는 직원의 이름과 월급을 출력하세요.
-- sol1) where절 subquery(in)
-- ver.유진
select de.emp_no, max(s.salary)
from salaries s, dept_emp de
where s.emp_no = de.emp_no and s.to_date = '9999-01-01' and de.to_date = '9999-01-01'
group by de.dept_no;

select d.dept_name, e.first_name, s.salary
from departments d, dept_emp de, employees e, salaries s
where d.dept_no = de.dept_no and de.emp_no = e.emp_no and e.emp_no = s.emp_no
	and (de.dept_no, s.salary) in (
		select de.dept_no, max(s.salary)
		from salaries s, dept_emp de
		where s.emp_no = de.emp_no and s.to_date = '9999-01-01' and de.to_date = '9999-01-01'
		group by de.dept_no
);

-- sol2) from절 subquery & join
select d.dept_name, e.first_name, s.salary
from departments d, dept_emp de, employees e, salaries s, (
	select de.dept_no, max(s.salary) as max_salary
	from dept_emp de, salaries s
    where de.emp_no = s.emp_no and de.to_date = '9999-01-01' and s.to_date = '9999-01-01'
    group by de.dept_no) e
where d.dept_no = de.dept_no and de.emp_no = e.emp_no and e.emp_no = s.emp_no
	and de.dept_no = e.dept_no
	and de.to_date = '9999-01-01' and s.to_date = '9999-01-01'
    and s.salary = e.max_salary;