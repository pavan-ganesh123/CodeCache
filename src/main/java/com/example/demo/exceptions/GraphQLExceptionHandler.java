package com.example.demo.exceptions;

import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.stereotype.Component;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;

@Component
public class GraphQLExceptionHandler extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(
            Throwable ex,
            DataFetchingEnvironment env
    ) {

        if (ex instanceof UserInputException) {
            return GraphqlErrorBuilder.newError(env)
                    .message(ex.getMessage())
                    .build();
        }

        return null;
    }
}