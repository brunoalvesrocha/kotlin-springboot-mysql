package com.example.basic

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.runApplication
import org.springframework.context.support.beans
import org.springframework.jdbc.core.JdbcOperations
import org.springframework.jdbc.core.queryForObject
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@SpringBootApplication
class BasicApplication

fun main(args: Array<String>) {
//    runApplication<BasicApplication>(*args)

    SpringApplicationBuilder()
            .sources(BasicApplication::class.java)
            .initializers(beans {

                bean {
                    ApplicationRunner {
                        val customerService = ref<CustomerService>()
                        arrayOf("Sebastien", "Danny", "Robert", "Hadi")
                                .map { Customer(name = it) }
                                .forEach {(customerService.insert(it))}

                        customerService.all().forEach({println(it)})
                    }
                }
            })
            .run(*args)
}


@Service
@Transactional
class JdbcCustomerService(private val jdbcOpetations: JdbcOperations) : CustomerService {
    override fun all(): Collection<Customer> = this.jdbcOpetations.query("SELECT * FROM CUSTOMERS") {
        rs, _ -> Customer(rs.getString("NAME"), rs.getLong("ID"))
    }

    override fun byId(id: Long): Customer? = this.jdbcOpetations.queryForObject("SELECT * FROM CUSTOMERS where ID = ?", id) {
        rs, _ -> Customer(rs.getString("NAME"), rs.getLong("ID"))
    }

    override fun insert(c: Customer) {
        this.jdbcOpetations.execute("INSERT INTO CUSTOMERS(NAME) values(?)") {
            it.setString(1, c.name)
            it.execute()
        }
    }

}

interface CustomerService {
    fun all() : Collection<Customer>
    fun byId(id: Long): Customer?
    fun insert(c: Customer)
}

data class Customer(val name: String, var id:Long? = null)
