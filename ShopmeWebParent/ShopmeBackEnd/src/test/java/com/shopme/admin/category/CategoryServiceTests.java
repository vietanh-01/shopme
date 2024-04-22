package com.shopme.admin.category;

import com.shopme.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class CategoryServiceTests {

    @MockBean
    private CategoryRepository repository;

    @InjectMocks
    private CategoryService service;

    @Test
    public void testUniqueCategory() {
        Integer id = null;
        String name = "Computers1";
        String alias = "computers1";

        Category category = new Category(id, name, alias);

        Mockito.when(repository.findByName(name)).thenReturn(null);
        Mockito.when(repository.findByAlias(alias)).thenReturn(null);

        String result = service.checkUnique(id, name, alias);

        assertThat(result).isEqualTo("ok");
    }


}
