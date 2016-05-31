/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cp.cloudtesterweb.util;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 *
 * @author Douglas
 * @param <T>
 */
public class EnumConverter<T extends Enum<T>> implements Converter {

    public final Enum<T> mEnum[];

    public EnumConverter(Enum<T> e[]) {
        this.mEnum = e;
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        for (Enum e : mEnum) {
            if (e.name().equals(value)) {
                return e;
            }
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value instanceof Enum) {
            Enum e = (Enum) value;
            return e.name();
        }
        return "";
    }

}
