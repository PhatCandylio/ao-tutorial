package com.atlassian.tutorial.ao.todo;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.DatabaseUpdater;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TodoServiceImplTest.TodoServiceImplTestDatabaseUpdater.class) // (1)
public class TodoServiceImplTest
{
    private static final String TODO_DESC = "This is a todo";

    private EntityManager entityManager;

    private ActiveObjects ao;

    private TodoServiceImpl todoService;

    @Before
    public void setUp() throws Exception
    {
        assertNotNull(entityManager);
        ao = new TestActiveObjects(entityManager);
        todoService = new TodoServiceImpl(ao);
    }

    @Test
    public void testAdd() throws Exception
    {
        final String description = TODO_DESC + "#1";

        assertEquals(1, ao.find(Todo.class).length);

        final Todo add = todoService.add(description);
        assertFalse(add.getID() == 0);

        ao.flushAll(); // clear all caches

        final Todo[] todos = ao.find(Todo.class);
        assertEquals(2, todos.length);
        assertEquals(add.getID(), todos[1].getID());
        assertEquals(description, todos[1].getDescription());
        assertEquals(false, todos[1].isComplete());
    }

    @Test
    public void testAll() throws Exception
    {
        assertEquals(1, todoService.all().size());

        final Todo todo = ao.create(Todo.class);
        todo.setDescription("Some todo");
        todo.save();

        ao.flushAll(); // clear all caches

        final List<Todo> all = todoService.all();
        assertEquals(2, all.size());
        assertEquals(todo.getID(), all.get(1).getID());
    }

    // (2)
    public static class TodoServiceImplTestDatabaseUpdater implements DatabaseUpdater
    {
        @Override
        public void update(EntityManager em) throws Exception
        {
            em.migrate(Todo.class);

            final Todo todo = em.create(Todo.class);
            todo.setDescription(TODO_DESC);
            todo.save();
        }
    }
}
