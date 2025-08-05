package com.jonyshev.intershop.config;

import com.jonyshev.intershop.model.Item;
import com.jonyshev.intershop.repository.ItemRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DataInit {

    @Bean
    public ApplicationRunner initItems(ItemRepository itemRepository) {
        return args -> itemRepository.count()
                .filter(count -> count == 0)
                .flatMapMany(rep -> itemRepository.saveAll(List.of(
                        Item.builder().title("Пицца").description("Вкусная пицца с сыром").imgPath("/images/pizza.png").price(BigDecimal.valueOf(550)).build(),
                        Item.builder().title("Бургер").description("Сочный бургер с говядиной").imgPath("/images/burger.png").price(BigDecimal.valueOf(350)).build(),
                        Item.builder().title("Суши").description("Набор роллов").imgPath("/images/sushi.png").price(BigDecimal.valueOf(750)).build(),
                        Item.builder().title("Паста").description("Паста карбонара").imgPath("/images/pasta.png").price(BigDecimal.valueOf(450)).build(),
                        Item.builder().title("Стейк").description("Говяжий стейк").imgPath("/images/steak.png").price(BigDecimal.valueOf(1200)).build(),
                        Item.builder().title("Салат").description("Овощной салат").imgPath("/images/salad.png").price(BigDecimal.valueOf(250)).build(),
                        Item.builder().title("Шаурма").description("Сочная шаурма с курицей").imgPath("/images/shaurma.png").price(BigDecimal.valueOf(300)).build(),
                        Item.builder().title("Картошка фри").description("Хрустящая картошка фри").imgPath("/images/fries.png").price(BigDecimal.valueOf(150)).build(),
                        Item.builder().title("Хот-дог").description("Классический хот-дог с сосиской").imgPath("/images/hotdog.png").price(BigDecimal.valueOf(200)).build(),
                        Item.builder().title("Лапша Wok").description("Лапша Wok с овощами и курицей").imgPath("/images/wok.png").price(BigDecimal.valueOf(400)).build(),
                        Item.builder().title("Том Ям").description("Острый тайский суп с креветками").imgPath("/images/tomyam.png").price(BigDecimal.valueOf(600)).build(),
                        Item.builder().title("Пельмени").description("Русские пельмени со сметаной").imgPath("/images/pelmeni.png").price(BigDecimal.valueOf(350)).build(),
                        Item.builder().title("Куриные крылышки").description("Острые куриные крылышки").imgPath("/images/wings.png").price(BigDecimal.valueOf(450)).build(),
                        Item.builder().title("Чизкейк").description("Классический ванильный чизкейк").imgPath("/images/cheesecake.png").price(BigDecimal.valueOf(300)).build(),
                        Item.builder().title("Мороженое").description("Пломбир с шоколадным топпингом").imgPath("/images/icecream.png").price(BigDecimal.valueOf(180)).build()
                )))
                .then()
                .subscribe();
    }
}