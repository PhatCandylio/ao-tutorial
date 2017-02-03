package com.atlassian.tutorial.ao.todo;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.transaction.TransactionCallback;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static com.google.common.base.Preconditions.*;

@Scanned
public final class TodoServlet extends HttpServlet
{
    @ComponentImport
    private final ActiveObjects ao;

    @Inject
    public TodoServlet(ActiveObjects ao)
    {
        this.ao = checkNotNull(ao);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        final PrintWriter w = res.getWriter();
        w.write("<h1>Todos</h1>");

        // the form to post more TODOs
        w.write("<form method=\"post\">");
        w.write("<input type=\"text\" name=\"task\" size=\"25\"/>");
        w.write("&nbsp;&nbsp;");
        w.write("<input type=\"submit\" name=\"submit\" value=\"Add\"/>");
        w.write("</form>");

        w.write("<ol>");

        ao.executeInTransaction(new TransactionCallback<Void>() // (1)
        {
            @Override
            public Void doInTransaction()
            {
                for (Todo todo : ao.find(Todo.class)) // (2)
                {
                    w.printf("<li><%2$s> %s </%2$s></li>", todo.getDescription(), todo.isComplete() ? "strike" : "strong");
                }
                return null;
            }
        });

        w.write("</ol>");
        w.write("<script language='javascript'>document.forms[0].elements[0].focus();</script>");

        w.close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        final String description = req.getParameter("task");
        ao.executeInTransaction(new TransactionCallback<Todo>() // (1)
        {
            @Override
            public Todo doInTransaction()
            {
                final Todo todo = ao.create(Todo.class); // (2)
                todo.setDescription(description); // (3)
                todo.setComplete(false);
                todo.save(); // (4)
                return todo;
            }
        });

        res.sendRedirect(req.getContextPath() + "/plugins/servlet/todo/list");
    }
}
