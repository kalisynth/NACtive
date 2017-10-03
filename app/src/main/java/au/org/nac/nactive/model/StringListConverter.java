package au.org.nac.nactive.model;

import java.util.ArrayList;

import io.requery.Converter;

/**
 * String Array Converter
 */

public class StringListConverter implements Converter<ArrayList<String>, String> {

    @SuppressWarnings("unchecked")
    @Override
    public Class<ArrayList<String>> getMappedType(){
        return (Class)ArrayList.class;
    }

    @Override
    public Class<String> getPersistedType(){
        return String.class;
    }

    @Override
    public Integer getPersistedSize(){
        return null;
    }

    @Override
    public String convertToPersisted(ArrayList<String> value){
        if(value == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int index = 0;
        for(Object string : value){
            if(index > 0) {
                sb.append(",");
            }
            sb.append(string);
            index++;
        }
        return sb.toString();
    }

    @Override
    public ArrayList<String> convertToMapped(Class<? extends ArrayList<String>> type,
                                             String value){
        ArrayList<String> list = new ArrayList<>();
        if(value != null){
            String[] parts = value.split(",");
            for(String part : parts){
                if(part.length() > 0){
                    list.add(part.trim());
                }
            }
        }
        return list;
    }
}
